/*
 * Copyright (C) 2026 Aleksei Balan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ab;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoilerplateTest {

  String html = "<!doctype html><html lang=\"en\"><head><title>Example Domain</title></head><body><div>" +
      "<p>This domain is for use in documentation examples without needing permission. Avoid use in operations.</p>" +
      "</div>\n<footer><address>\n<p>Email: <a href=\"mailto:webmaster@example.com\">Jon Doe</a>.</p>\n" +
      "<p>Address: 1122 Main Street, New York</p>\n</address></footer>\n</body></html>\n";

  static final Pattern TITLE = Pattern.compile(".*?<title>(.*?)</title>.*", Pattern.DOTALL);

  @Test
  void title() {
    Matcher matcher = TITLE.matcher(html);
    matcher.matches();
    String title = matcher.group(1);
    assertEquals("Example Domain", title);
  }

  static final Pattern BODY = Pattern.compile(".*?<body>(.*?)</body>.*", Pattern.DOTALL);
  static final Pattern FOOTER = Pattern.compile(".*?<footer>(.*?)</footer>.*", Pattern.DOTALL);
  static final Pattern ADDRESS = Pattern.compile(".*?<address>(.*?)</address>.*", Pattern.DOTALL);
  static final Pattern EMAIL = Pattern.compile(".*?mailto:(.*?)\">.*", Pattern.DOTALL);

  @Test
  void email() {
    Matcher bodyMatcher = BODY.matcher(html);
    bodyMatcher.matches();
    String body = bodyMatcher.group(1);
    Matcher footerMatcher = FOOTER.matcher(body);
    footerMatcher.matches();
    String footer = footerMatcher.group(1);
    Matcher addressMatcher = ADDRESS.matcher(footer);
    addressMatcher.matches();
    String address = addressMatcher.group(1);
    Matcher emailMatcher = EMAIL.matcher(address);
    emailMatcher.matches();
    String email = emailMatcher.group(1);
    assertEquals("webmaster@example.com", email);
  }

}
