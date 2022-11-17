package com.allendowney.thinkdast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

public class WikiPhilosophy {

    final static List<String> visited = new ArrayList<String>();
    final static WikiFetcher wf = new WikiFetcher();

    /**
     * Tests a conjecture about Wikipedia and Philosophy.
     * <p>
     * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
     * <p>
     * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     * that does not exist, or when a loop occurs
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String destination = "https://en.wikipedia.org/wiki/Philosophy";
        String source = "https://en.wikipedia.org/wiki/Java_(programming_language)";

        testConjecture(destination, source, 30);
    }

    /**
     * Starts from given URL and follows first link until it finds the destination or exceeds the limit.
     *
     * @param destination
     * @param source
     * @throws IOException
     */
    public static void testConjecture(String destination, String source, int limit) throws IOException {
        String currentUrl = source;
        for (int i = 0; i < limit; i++) {
            if (visited.contains(currentUrl)) {
                System.out.println("Already find this url. Exit");
                return;
            } else {
                visited.add(currentUrl);
            }

            Elements elements = wf.fetchWikipedia(currentUrl);
            Element elWithUrl = getFirstValidElement(elements);
            currentUrl = elWithUrl.attr("abs:href");
            System.out.println(currentUrl);

            if (currentUrl.equals(destination)) {
                System.out.println("Done");
                break;
            }
        }
    }

    private static Element getFirstValidElement(Elements elements) {
        for (Element element : elements) {
            WikiNodeIterable iterable = new WikiNodeIterable(element);
            int countOfToken = 0;
            for (Node node : iterable) {
                if (node instanceof TextNode) {
                    countOfToken = processTextNode(countOfToken, (TextNode) node);
                } else if (node instanceof Element) {
                    Element el = processElementNode(countOfToken, (Element) node);
                    if (el != null) {
                        return el;
                    }
                }
            }
        }
        return null;
    }

    private static int processTextNode(int countOfToken, TextNode node) {
        StringTokenizer st = new StringTokenizer(node.text(), " ()", true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            // System.out.print(token);
            if (token.equals("(")) {
                countOfToken++;
            }
            if (token.equals(")")) {
                if (countOfToken == 0) {
                    System.err.println("Warning: unbalanced parentheses.");
                }
                countOfToken--;
            }
        }
        return countOfToken;
    }

    private static Element processElementNode(int countOfToken, Element el) {
        if (isUrl(el) && isValidUrl(countOfToken, el)) {
            return el;
        }
        return null;
    }

    private static boolean isUrl(Element el) {
        return el.tag().equals(Tag.valueOf("a"));
    }

    private static boolean isValidUrl(int countOfToken, Element elWithUrl) {
        // with href
        if (hasHref(elWithUrl)) return false;
        // in Italic
        if (isItalic(elWithUrl)) return false;
        // a bookmark
        if (isBookmark(elWithUrl)) return false;
        // a Wikipedia help page
        if (isHelpPage(elWithUrl)) return false;
        // in parenthesis
        if (isInParens(countOfToken)) return false;
        // have already visited
        if (isVisitedUrl(elWithUrl)) return false;
        return true;
    }

    private static boolean isVisitedUrl(Element elWithUrl) {
        return visited.contains(elWithUrl.attr("abs:href"));
    }

    private static boolean isInParens(int countOfToken) {
        return countOfToken != 0;
    }

    private static boolean isItalic(Element elWithUrl) {
        boolean inItalic = false;
        for (Element elt = elWithUrl; elt != null; elt = elt.parent()) {
            if (elt.tagName().equals("i") || elt.tagName().equals("em")) {
                inItalic = true;
            }
            if (inItalic) {
                break;
            }
        }

        return inItalic;
    }

    private static boolean isHelpPage(Element elWithUrl) {
        return elWithUrl.attr("href").startsWith("/wiki/Help:");
    }

    private static boolean isBookmark(Element elWithUrl) {
        return elWithUrl.attr("href").startsWith("#");
    }

    private static boolean hasHref(Element elWithUrl) {
        return elWithUrl.attr("href").length() == 0;
    }
}
