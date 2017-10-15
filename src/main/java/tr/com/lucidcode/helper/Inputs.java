package tr.com.lucidcode.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adinema on 14/10/17.
 */
public class Inputs {



    public static List<String> getRatioList(){
        List<String> list = new ArrayList<String>();
        list.add("ROCE");
        list.add("DILUTED EPS");
        list.add("EQUITY_SHARE_CAPITAL");
        list.add("FACE VALUE");

        return list;
    }
}
