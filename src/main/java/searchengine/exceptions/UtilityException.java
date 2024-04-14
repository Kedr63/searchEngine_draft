package searchengine.exceptions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UtilityException {
    public static String getShortMessageOfException(Exception e) {
        StringBuilder builder = new StringBuilder();
        String[] arrayMessage = e.getMessage().split(":");
        if (arrayMessage.length >=2){
            List<String> stringList = Arrays.stream(arrayMessage).collect(Collectors.toList());
            builder.append(stringList.get(stringList.size() - 2)).append(" - ");
            builder.append(stringList.get(stringList.size() - 1));
            return builder.toString();
        } else
            return e.getMessage();
    }
}
