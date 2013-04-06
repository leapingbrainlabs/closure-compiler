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
 */
public class PoMessageBundle implements MessageBundle {
  private final Map<String, JsMessage> messages;
  private final JsMessage.IdGenerator idGenerator;
  private static boolean pluralMessageNeedsEnd = false;

  private static final String PLURAL_VAR_PREFIX = "#: pluralVar=";
  private static final String ID_PREFIX = "#: id=";
  private static final String SINGULAR_TRANSLATION_PREFIX = "msgstr ";
  private static final String PLURAL_TRANSLATION_PREFIX = "msgstr[";

  /**
   * Creates an instance and initializes it with the messages in a PO file.
   *
   * @param po  the PO file as a byte stream
   * @param projectId  the translation console project id (i.e. name)
   */
  public PoMessageBundle(InputStream po, @Nullable String projectId) {
    Preconditions.checkState(!"".equals(projectId));
    this.messages = Maps.newHashMap();
    this.idGenerator = new GoogleJsMessageIdGenerator(projectId);

    try {
      parsePoTargetMsgs(readString(po), this.messages);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

 /**
   * Parses the content of a translated PO file.
   *
   * @param poContent The PO content to parse.
   * @throws PoException If there's an error parsing the data.
   */
  static void parsePoTargetMsgs(
          String poContent,
          Map<String, JsMessage> messages) {
    Scanner scanner = new Scanner(poContent);
    scanner.useDelimiter("\n\n");

    while (scanner.hasNext()) {
      JsMessage message = parseMessage(scanner.next());
      messages.put(message.getId(), message);
    }
  }

  /**
   *
   * Put a PO message example here
   *
   * @param messageContent
   * @return
   */
  private static JsMessage parseMessage(String messageContent) {
    JsMessage.Builder msgBuilder = new JsMessage.Builder();
    String id = null;
    String meaning = null;
    String desc = null;
    String pluralVar = null;

    // TODO: Make this an enum or something and incorporate the plural status
    // into it
    String currentParseStatus = "standard";

    Scanner scanner = new Scanner(messageContent);

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();


      if (line.startsWith("\"") && currentParseStatus.equalsIgnoreCase("translation")) {
        parseTranslationString(line.trim().substring(1, line.length() -1), msgBuilder, pluralVar != null);
        continue;
      } else {
        currentParseStatus = "standard";
      }

      // Check if there is a previous multi-line plural translation that needs
      // to be closed
      endPluralTranslation(msgBuilder);

      if (line.startsWith(ID_PREFIX)) {
        id = line.substring(ID_PREFIX.length()).trim();
      } else if (line.startsWith(PLURAL_VAR_PREFIX)) {
        pluralVar = line.substring(PLURAL_VAR_PREFIX.length()).trim();
        msgBuilder.appendStringPart("{");
        msgBuilder.appendStringPart(pluralVar);
        msgBuilder.appendStringPart(" ,plural, offset:0  ");
      } else if (line.startsWith(SINGULAR_TRANSLATION_PREFIX)) {
        currentParseStatus = "translation";
        parseTranslationLine(line, msgBuilder, false);
      } else if (line.startsWith(PLURAL_TRANSLATION_PREFIX)) {
        currentParseStatus = "translation";
        parsePluralTranslation(line, msgBuilder);
      }
    }

    // Check if there is a previous multi-line plural translation that still
    // needs to be closed
    endPluralTranslation(msgBuilder);

    if (pluralVar != null) {
      msgBuilder.appendStringPart("}");
    }

    msgBuilder.setDesc(desc);
    msgBuilder.setMeaning(meaning);
    msgBuilder.setKey(id);

    JsMessage message = msgBuilder.build();

    return message;
  }

  private static void parsePluralTranslation(
          String translationLines,
          JsMessage.Builder msgBuilder) {

    Scanner scanner = new Scanner(translationLines);
    scanner.useDelimiter("\n");

    while (scanner.hasNext()) {
      parsePluralTranslationLine(scanner.nextLine(),msgBuilder);
    }
  }

  private static void parseTranslationLine(
          String translationLine,
          JsMessage.Builder msgBuilder,
          boolean inPlural) {
    Scanner scanner = new Scanner(translationLine);
    scanner.useDelimiter("\"");

    // Consume opening quotation
    if (!scanner.hasNext()) return;
    scanner.next();

    // Store beginning of string
    if (!scanner.hasNext()) return;
    String s = scanner.next();

    // Concatenate the entire string until the final, closing quotation is found
    while (scanner.hasNext()) {
      s = s + "\"" + scanner.next();
    }

    parseTranslationString(s, msgBuilder, inPlural);
  }

  private static void parseTranslationString(String translationString,
          JsMessage.Builder msgBuilder,
          boolean inPlural) {
    Scanner scanner = new Scanner(translationString);
    scanner.useDelimiter(Pattern.compile("\\{\\$|\\}"));
    boolean inVariableToken = translationString.startsWith("{");

    while (scanner.hasNext()) {
      if (inVariableToken && inPlural) {
        String token = scanner.next();
        msgBuilder.appendStringPart("{" + token + "}");
      } else if (inVariableToken) {
        String token = scanner.next();
        token = JsMessageVisitor.toLowerCamelCaseWithNumericSuffixes(token);
        msgBuilder.appendPlaceholderReference(token);
      } else {
        msgBuilder.appendStringPart(unescapeString(scanner.next()));
      }
      inVariableToken = !inVariableToken;
    }
  }

  private static void parsePluralTranslationLine(
          String translationLine,
          JsMessage.Builder msgBuilder) {
    int n;
    Scanner scanner = new Scanner(translationLine);
    scanner.useDelimiter("\\[|\\]");
    try {
      scanner.next();
    } catch (InputMismatchException e) {
      throw new PoException(
              "Incorrect plural translation line: " + translationLine);
    }
    n = scanner.nextInt();

    if (n == 0) {
      msgBuilder.appendStringPart(" =1 ");
    } else {
      msgBuilder.appendStringPart(" other ");
    }
    startPluralTranslation(msgBuilder);
    parseTranslationLine(scanner.nextLine(), msgBuilder, true);
  }

  private static void startPluralTranslation(JsMessage.Builder msgBuilder) {
    pluralMessageNeedsEnd = true;
    msgBuilder.appendStringPart("{");
  }

  private static void endPluralTranslation(JsMessage.Builder msgBuilder) {
    if (pluralMessageNeedsEnd) {
      pluralMessageNeedsEnd = false;
      msgBuilder.appendStringPart("}");
    }
  }

  public static String readString(InputStream inputStream) throws IOException {
      ByteArrayOutputStream into = new ByteArrayOutputStream();
      byte[] buf = new byte[4096];
      for (int n; 0 < (n = inputStream.read(buf));) {
          into.write(buf, 0, n);
      }
      into.close();
      return new String(into.toByteArray(), "UTF-8");
  }

  static String unescapeString(String s) {
    return s.replace("\\\"", "\"").replace("\\n", "\n");
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

}
