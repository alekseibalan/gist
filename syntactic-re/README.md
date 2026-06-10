# syntactic-re
Syntax candy class that wraps regex boilerplate.

syntactic Re
```java
import ab.Re;

Re TITLE = new Re(".*?<title>(.*?)</title>.*");

String title = TITLE.from(html);

Re BODY = new Re(".*?<body>(.*?)</body>.*");
Re FOOTER = new Re(".*?<footer>(.*?)</footer>.*");
Re ADDRESS = new Re(".*?<address>(.*?)</address>.*");
Re EMAIL = new Re(".*?mailto:(.*?)\">.*");

String email = EMAIL.from(ADDRESS.from(FOOTER.from(BODY.from(html))));
```

vanilla regex
```java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

Pattern TITLE = Pattern.compile(".*?<title>(.*?)</title>.*", Pattern.DOTALL);

Matcher matcher = TITLE.matcher(html);
matcher.matches();
String title = matcher.group(1);

Pattern BODY = Pattern.compile(".*?<body>(.*?)</body>.*", Pattern.DOTALL);
Pattern FOOTER = Pattern.compile(".*?<footer>(.*?)</footer>.*", Pattern.DOTALL);
Pattern ADDRESS = Pattern.compile(".*?<address>(.*?)</address>.*", Pattern.DOTALL);
Pattern EMAIL = Pattern.compile(".*?mailto:(.*?)\">.*", Pattern.DOTALL);

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
```
