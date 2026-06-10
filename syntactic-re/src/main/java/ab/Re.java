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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Syntax candy class that wraps regex boilerplate.
 *
 * static final Pattern TITLE = Pattern.compile(".*?<title>(.*?)</title>.*", Pattern.DOTALL);
 * Matcher matcher = TITLE.matcher(html);
 * matcher.matches();
 * String title = matcher.group(1);
 *
 * static final Re TITLE = new Re(".*?<title>(.*?)</title>.*");
 * String title = TITLE.from(html);
 *
 * String email = EMAIL.from(ADDRESS.from(FOOTER.from(BODY.from(html))));
 */
public class Re {

  final Pattern pattern;

  public Re(String regex) {
    pattern = Pattern.compile(regex, Pattern.DOTALL);
  }

  public String from(String s) {
    return get(pattern, s);
  }

  public static String get(Pattern pattern, String s) {
    Matcher matcher = pattern.matcher(s);
    boolean matches = matcher.matches();
    assert matches;
    return matcher.group(1);
  }

}
