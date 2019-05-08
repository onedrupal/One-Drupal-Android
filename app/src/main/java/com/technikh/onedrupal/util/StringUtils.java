package com.technikh.onedrupal.util;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import java.util.List;

public class StringUtils {

    private static String TAG = "ApiUtils";

    public static String implode(String separator, List<String> data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.size() - 1; i++) {
            //data.length - 1 => to not add separator at the end
            if (!data.get(i).matches(" *")) {//empty string are ""; " "; "  "; and so on
                sb.append(data.get(i));
                sb.append(separator);
            }
        }
        sb.append(data.get(data.size() - 1).trim());
        return sb.toString();
    }
}
