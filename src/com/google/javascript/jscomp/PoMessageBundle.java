/*
 * Copyright 2006 The Closure Compiler Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.javascript.jscomp;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

/**
 * A MessageBundle that parses messages from an GNU Gettext PO file
 *
 * PO files have some advantages and limitations
 *
 * Unlike XLIFF, they do support plural messages.
 * This implementation requires that plural
 * designations be explicit values (and not "few" or "many", etc).
 *
 */
public class PoMessageBundle implements MessageBundle {
  private final Map<String, JsMessage> messages;
  private final JsMessage.IdGenerator idGenerator;

  private static final String PLURAL_VAR_PREFIX = "#: pluralVar=";
  private static final String ID_PREFIX = "#: id=";
  private static final String SOURCE_MESSAGE_PREFIX = "msgid ";
  private static final String SINGULAR_TRANSLATION_PREFIX = "msgstr ";
  private static final String PLURAL_TRANSLATION_PREFIX = "msgstr[";
  private static final String MESSAGE_CONTEXT_PREFIX = "msgctxt ";

  private enum ParseStatus {
    MESSAGE,
    CONTEXT,
    SOURCE_MESSAGE,
    SINGULAR_TRANSLATION,
    OUTER_PLURAL_TRANSLATION,
    INNER_PLURAL_TRANSLATION
  }

  private ParseStatus currentParseStatus;
  private JsMessage.Builder currentMsgBuilder;
  private StringBuilder currentDescription;
  private String currentMsgSuffix;

  /**
   * Creates an instance and initializes it with the messages in a PO file.
   *
   * @param po  the PO file as a byte stream
   * @param projectId  the translation console project id (i.e. name)
   */
  public PoMessageBundle(InputStream poContent, @Nullable String projectId) {
    Preconditions.checkState(!"".equals(projectId));
    this.messages = Maps.newHashMap();
    this.idGenerator = new GoogleJsMessageIdGenerator(projectId);

    parsePoTargetMsgs(poContent);
  }

  @Override
  public JsMessage getMessage(String id) {
    return messages.get(id);
  }

  @Override
  public JsMessage.IdGenerator idGenerator() {
    return idGenerator;
  }

  @Override
  public Iterable<JsMessage> getAllMessages() {
    return Iterables.unmodifiableIterable(messages.values());
  }

 /**
   * Parses the content of a translated PO file.
   *
   * For more information about PO files, see:
   * http://www.gnu.org/software/gettext/manual/html_node/PO-Files.html
   *
   * @param poContent The PO content to parse.
   * @throws PoException If there's an error parsing the data.
   */
  private void parsePoTargetMsgs(InputStream poContent) {
    Scanner scanner = new Scanner(poContent, "UTF8");
    scanner.useDelimiter("\n\n");

    while (scanner.hasNext()) {
      parseMessage(scanner.next());
    }
  }

  /**
   *
   * Parses a single PO message
   *
   * @param messageContent The string for an entire translatable message
   * @return The parsed message
   */
  private void parseMessage(String messageContent) {
    currentMsgBuilder = new JsMessage.Builder();
    currentDescription = new StringBuilder();
    currentMsgSuffix = "";

    Scanner scanner = new Scanner(messageContent);

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();

      // Check if the current line is a continuing translation
      if (line.startsWith("\"")) {
        parseContinuingLine(line);
        continue;
      }

      // Check if there is a previous multi-line plural translation that needs
      // to be closed
      endTranslation();

      parseLineByPrefix(line);
    }

    // End any remaining open translations. (Requires calling this method twice)
    endTranslation();
    endTranslation();

    currentMsgBuilder.appendStringPart(currentMsgSuffix);
    currentMsgBuilder.setDesc(currentDescription.toString());
    JsMessage message = currentMsgBuilder.build();
    messages.put(message.getId(), message);
  }

  void parseLineByPrefix(String line) {
    if (line.startsWith(ID_PREFIX)) {
      currentMsgBuilder.setKey(line.substring(ID_PREFIX.length()).trim());
    } else if (line.startsWith(PLURAL_VAR_PREFIX)) {
      String pluralVar = line.substring(PLURAL_VAR_PREFIX.length()).trim();
      currentMsgBuilder.appendStringPart(
              "{" + pluralVar + " ,plural, offset:0  ");
      currentMsgSuffix = "}";
    } else if (line.startsWith(SOURCE_MESSAGE_PREFIX)) {
      currentParseStatus = ParseStatus.SOURCE_MESSAGE;
    } else if (line.startsWith(SINGULAR_TRANSLATION_PREFIX)) {
      currentParseStatus = ParseStatus.SINGULAR_TRANSLATION;
      parseTranslationString(unQuote(line));
    } else if (line.startsWith(PLURAL_TRANSLATION_PREFIX)) {
      currentParseStatus = ParseStatus.OUTER_PLURAL_TRANSLATION;
      parsePluralTranslationLine(line);
    } else if (line.startsWith(MESSAGE_CONTEXT_PREFIX)) {
      currentDescription.append(unQuote(line));
      currentParseStatus = ParseStatus.CONTEXT;
    }
    // Other lines should be ignored.
  }

  /**
   * Parse a continuation line.
   *
   */
  private void parseContinuingLine(String line) {
    if (currentParseStatus == ParseStatus.SINGULAR_TRANSLATION) {
      parseTranslationString(unQuote(line));
    } else if (currentParseStatus == ParseStatus.INNER_PLURAL_TRANSLATION) {
      parseTranslationString(unQuote(line));
    } else if (currentParseStatus == ParseStatus.CONTEXT) {
      currentDescription.append(unQuote(line));
    } else if (currentParseStatus == ParseStatus.SOURCE_MESSAGE) {
      // We don't actually do anything with this.
    } else {
      throw new PoException("Unexpected line continuation: " + line);
    }
  }

  /**
   * Returns everything in between the first and last quote of the string,
   * ignoring any potential placeholders.
   *
   * Examples:
   * msgstr "this is translated" => this is translated
   * "also translated" => also translated
   *
   * @return The unquoted translation
   * @throws PoException When the translation is not between two quotes
   */
  private static String unQuote(String line) {
    int firstQuote = line.indexOf("\"");
    int lastQuote = line.lastIndexOf("\"");
    if (firstQuote == -1 || firstQuote == lastQuote) {
      throw new PoException(
              "Malformed translation. Line is not properly quoted: " + line);
    }
    return line.substring(firstQuote + 1, lastQuote);
  }

  /**
   * Parses a string into the message builder.
   *
   * This method will parse placeholders, inserting them as placeholders
   * references unless the parse status is INNER_PLURAL_TRANSLATION, when it
   * will add them as normal text, so that the message may follow ICU format.
   */
  private void parseTranslationString(String translationString) {
    Scanner scanner = new Scanner(translationString);
    scanner.useDelimiter("\\{\\$|\\}");
    boolean inVariableToken = translationString.startsWith("{");
    boolean inPlural =
        currentParseStatus == ParseStatus.INNER_PLURAL_TRANSLATION;

    while (scanner.hasNext()) {
      if (inVariableToken && inPlural) {
        // Write the variable in ICU format
        String token = scanner.next();
        currentMsgBuilder.appendStringPart("{" + token + "}");
      } else if (inVariableToken) {
        // Add the variable as a placeholder
        String token = scanner.next();
        token = JsMessageVisitor.toLowerCamelCaseWithNumericSuffixes(token);
        currentMsgBuilder.appendPlaceholderReference(token);
      } else {
        // Append the string to the message
        currentMsgBuilder.appendStringPart(unescapeString(scanner.next()));
      }
      // Switch variable context as we pass placeholder delimiters
      inVariableToken = !inVariableToken;
    }
  }

  /**
   * Parse a plural translation line into the message builder using ICU format.
   *
   * Eventually, it should support "N" as the "other" case, and any other
   * explicit numbers.
   *
   * The pipeline works now, though with English source using "0" as the label
   * for the explicit one case and "1" as the other case.
   */
  private void parsePluralTranslationLine(String translationLine) {
    Scanner scanner = new Scanner(translationLine);
    scanner.useDelimiter("\\[|\\]");
    String pluralCase;
    try {
      scanner.next(); // Consume up through the first bracket. Should be left.
    } catch (InputMismatchException e) {
      throw new PoException(
              "Incorrect plural translation line: " +translationLine);
    }

    // This should consume up through the second bracket, returning the plural
    // case between the first and second.
    try {
      pluralCase = scanner.next();
    } catch (NoSuchElementException e) {
      throw new PoException(
              "Incorrect plural translation line: " +translationLine);
    }

    //TODO fully support ICU explicit numbers
    if (pluralCase.equalsIgnoreCase("0")) {
      currentMsgBuilder.appendStringPart(" =1 ");
    } else {
      currentMsgBuilder.appendStringPart(" other ");
    }

    startPluralTranslation();
    parseTranslationString(unQuote(scanner.nextLine()));
  }

  /**
   * Begin a plural translation.
   */
  private void startPluralTranslation() {
    currentParseStatus = ParseStatus.INNER_PLURAL_TRANSLATION;
    currentMsgBuilder.appendStringPart("{");
  }

  /**
   * End the current translation, updating the current parse status
   *
   */
  private void endTranslation() {
    if (currentParseStatus == ParseStatus.OUTER_PLURAL_TRANSLATION) {
      currentMsgBuilder.appendStringPart("}");
      currentParseStatus = ParseStatus.INNER_PLURAL_TRANSLATION;
    }
    else if (currentParseStatus == ParseStatus.INNER_PLURAL_TRANSLATION) {
      currentMsgBuilder.appendStringPart("}");
    }
    currentParseStatus = ParseStatus.MESSAGE;
  }

  /**
   * Unescape backslashes and newlines
   */
  private static String unescapeString(String s) {
    return s.replace("\\\"", "\"").replace("\\n", "\n");
  }

}
