package tr.com.lucidcode.pojo;

import java.util.Date;

/**
 * Created by adinema on 09/07/17.
 */
public class MoneyControlDataCSV {

    private String ratio;
    private String scrip;



    private Object value17;
    private Object value16;
    private Object value15;
    private Object value14;
    private Object value13;
    private Object value12;

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getScrip() {
        return scrip;
    }

    public void setScrip(String scrip) {
        this.scrip = scrip;
    }

    public Object getValue17() {
        return value17;
    }

    public void setValue17(Object value17) {
        this.value17 = value17;
    }

    public Object getValue16() {
        return value16;
    }

    public void setValue16(Object value16) {
        this.value16 = value16;
    }

    public Object getValue15() {
        return value15;
    }

    public void setValue15(Object value15) {
        this.value15 = value15;
    }

    public Object getValue14() {
        return value14;
    }

    public void setValue14(Object value14) {
        this.value14 = value14;
    }

    public Object getValue13() {
        return value13;
    }

    public void setValue13(Object value13) {
        this.value13 = value13;
    }

    public Object getValue12() {
        return value12;
    }

    public void setValue12(Object value12) {
        this.value12 = value12;
    }

    public String toString(){
        return ratio + " " + scrip;
    }
}
