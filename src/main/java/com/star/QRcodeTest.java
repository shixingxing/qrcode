package com.star;

import javax.management.RuntimeErrorException;

public class QRcodeTest {

    static String image_parm = "";
    static String save_path = "";
    static String url_parm = "";

    public static void main(String[] args) throws Exception {
        if (args.length != 0) {
            for (String arg : args) {
                if (arg.startsWith("url=")) {
                    url_parm = arg.trim().toLowerCase().split("=")[1];
                } else if (arg.startsWith("image=")) {
                    image_parm = arg.trim().toLowerCase().split("=")[1];
                } else if (arg.startsWith("save=")) {
                    save_path = arg.trim().toLowerCase().split("=")[1];
                }
            }
            if (url_parm == null || url_parm.isEmpty()) {
                throw new RuntimeErrorException((Error) null, "url null!");
            }
            QRCodeUtil.encode(url_parm, image_parm, save_path, true);
            return;
        }
        throw new RuntimeErrorException((Error) null, "url or image save path are mandatory!");
    }

}
