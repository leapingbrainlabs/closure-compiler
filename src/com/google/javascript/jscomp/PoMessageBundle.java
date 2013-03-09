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
 * A MessageBundle that parses messages from an XML Translation Bundle (XTB)
 * file.
 *
 */
@SuppressWarnings("sunapi")
public class PoMessageBundle implements MessageBundle {

  private final Map<String, JsMessage> messages;
  private final JsMessage.IdGenerator idGenerator;

  /**
   * Creates an instance and initializes it with the messages in an XTB file.
   *
   * @param xtb  the XTB file as a byte stream
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
   * Parses the content of a translated XLIFF file and creates a SoyMsgBundle.
   *
   * @param poContent The PO content to parse.
   * @throws PoException If there's an error parsing the data.
   * @throws SoyMsgException If there's an error in parsing the data.
   */
  static void parsePoTargetMsgs(String poContent, Map<String, JsMessage> messages) {

    Scanner scanner = new Scanner(poContent);
    scanner.useDelimiter("\n\n");

    while (scanner.hasNext()) {
      JsMessage message = parseMessage(scanner.next());
      System.out.println("New message: " + message.getId() + ": " + message.toString());
      messages.put(message.getId(), message);
    }
  }

  private static JsMessage parseMessage(String messageContent) {

    JsMessage.Builder msgBuilder = new JsMessage.Builder();
    String id = null;
    String meaning = null;
    String desc = null;

    Scanner scanner = new Scanner(messageContent);

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      if (line.startsWith("#: id=")) {
        id = line.substring(6);
      } else if (line.startsWith("msgstr ")) {
        parseTranslationLine(line, msgBuilder);
      } else if (line.startsWith("msgstr[")) {
        parsePluralTranslation(line, msgBuilder);
      }
    }

    msgBuilder.setDesc(desc);
    msgBuilder.setMeaning(meaning);
    msgBuilder.setKey(id);

    JsMessage message = msgBuilder.build();

    return message;
  }

  private static void parsePluralTranslation(String translationLines, JsMessage.Builder msgBuilder) {

    Scanner scanner = new Scanner(translationLines);
    scanner.useDelimiter("\n");

    while (scanner.hasNext()) {
      parsePluralTranslationLine(scanner.nextLine(),msgBuilder);
    }
  }

  private static void parseTranslationLine(String translationLine, JsMessage.Builder msgBuilder) {
    Scanner scanner = new Scanner(translationLine);
    scanner.useDelimiter("'|\"");

    scanner.next();
    parseTranslationString(scanner.next(), msgBuilder);
  }

  private static void parseTranslationString(String translationString, JsMessage.Builder msgBuilder) {
    Scanner scanner = new Scanner(translationString);
    scanner.useDelimiter(Pattern.compile("\\{\\$|\\}"));
    boolean inVariableToken = false;

    while (scanner.hasNext()) {
      if (inVariableToken) {
        msgBuilder.appendPlaceholderReference(scanner.next());
      } else {
        msgBuilder.appendStringPart(scanner.next());
      }
      inVariableToken = !inVariableToken;
    }

    if (scanner.hasNextLine()) {
      String remainder = scanner.nextLine();
      if (remainder.length() > 0) {
        msgBuilder.appendStringPart(remainder);
      }
    }
  }

  private static void parsePluralTranslationLine(String translationLine, JsMessage.Builder msgBuilder) {
    int n;
    Scanner scanner = new Scanner(translationLine);
    scanner.useDelimiter("\\[|\\]");
    try {
      scanner.next();
    } catch (InputMismatchException e) {
      throw new PoException("Incorrect plural translation line: ".concat(translationLine));
    }
    n = scanner.nextInt();

    parseTranslationLine(scanner.nextLine(), msgBuilder);

  }

  public static String readString(InputStream inputStream) throws IOException {

      ByteArrayOutputStream into = new ByteArrayOutputStream();
      byte[] buf = new byte[4096];
      for (int n; 0 < (n = inputStream.read(buf));) {
          into.write(buf, 0, n);
      }
      into.close();
      return new String(into.toByteArray(), "UTF-8"); // Or whatever encoding
  }


  @Override
  public JsMessage getMessage(String id) {
    JsMessage message = messages.get(id);
    if (message == null) {
      System.out.println(id + " = null");
    } else {
      System.out.println(id + " " + message.toString());
    }

    return messages.get(id);
  }

  @Override
  public JsMessage.IdGenerator idGenerator() {
    return idGenerator;
  }

  @Override
  public Iterable<JsMessage> getAllMessages() {
    System.out.println("get all messages");
    return Iterables.unmodifiableIterable(messages.values());
  }

}
