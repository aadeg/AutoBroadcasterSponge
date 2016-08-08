package io.github.aadeg.autobroadcaster.utils;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    /**
     * @param text Text object to serialize
     * @return String with Minecraft formatting codes (ex: &6, &3, ...)
     */
    public static String serializeText(Text text){
        return TextSerializers.FORMATTING_CODE.serialize(text);
    }

    /**
     * @param str String with Minecaft formatting codes (ex: &6, &3, ...)
     * @param parseUrl If true the URLs (http://...) will be converted in clickable links
     * @return Text object
     */
    public static Text deserializeText(String str, boolean parseUrl){
        if (!parseUrl)
            return TextSerializers.FORMATTING_CODE.deserialize(str);

        final String URL_REGEX = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&\\/=]*)";
        Pattern urlPattern = Pattern.compile(URL_REGEX);
        Matcher m = urlPattern.matcher(str);
        List<String> urls = new ArrayList<String>();    // List of the urls in the string.

        while(m.find())
            urls.add(m.group());

        if (urls.isEmpty())     // URL not found
            return deserializeText(str);

        String[] nonUrlStr = str.split(URL_REGEX);      // Slitting the message for remove urls.

        boolean startWithUrl = str.startsWith(urls.get(0));     // true if the message start with an url

        // Links are merged with the rest of the message in the original position
        return mergeToText(nonUrlStr, urls.toArray(new String[urls.size()]), startWithUrl);
    }

    /**
     * URLs are not converted in clickable links.
     * @param str String with Minecaft formatting codes (ex: &6, &3, ...)
     * @return Text object
     */
    public static Text deserializeText(String str){
        return deserializeText(str, false);
    }

    /**
     * Returns the Text with a clickable link that redirect to the url passed.
     * @param url
     * @param format
     * @return
     * @throws MalformedURLException
     */
    private static Text deserializeURLToText(String url, TextFormat format) throws MalformedURLException {
        return Text.builder(url).onClick(TextActions.openUrl(new URL(url))).format(format).build();
    }


    private static  Text mergeToText(String[] strs, String[] urls, boolean startWithUrl) {
        Text.Builder builder = Text.builder();
        mergeToText(builder, strs, urls, startWithUrl, 0, 0);
        return builder.build();
    }

    private static void mergeToText(Text.Builder builder, String[] strs, String[] urls, boolean startWithUrl, int startStrs, int startUrls) {
        if (startStrs > strs.length && startUrls > urls.length)
            return;

        TextFormat lastTextFormat = TextFormat.NONE;

        // Text formatting doesn't propagate from url to the next non-url string
        // Text formatting propagates from non-url string to the next url
        if (startWithUrl){
            if (startUrls < urls.length)
                try {
                    Text t = deserializeURLToText(urls[startUrls], lastTextFormat);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            if (startStrs < strs.length)
                builder.append(deserializeText(strs[startStrs]));
        } else {
            if (startStrs < strs.length) {
                Text t = deserializeText(strs[startStrs]);
                builder.append(t);
                lastTextFormat = lastTextFormat(t);
            }
            if (startUrls < urls.length)
                try {
                    builder.append(deserializeURLToText(urls[startUrls], lastTextFormat));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
        }

        mergeToText(builder, strs, urls, startWithUrl, startStrs + 1, startUrls + 1);
    }

    /**
     * Retrieves the format of the last children
     * @param t
     * @return
     */
    private static TextFormat lastTextFormat(Text t){
        if (t.getChildren().isEmpty())
            return t.getFormat();

        ImmutableList<Text> children = t.getChildren();
        return lastTextFormat(children.get(children.size() - 1));
    }
}
