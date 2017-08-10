package tr.com.lucidcode.pojo;

import java.util.Date;

/**
 * Created by adinema on 09/07/17.
 */
public class DateValue implements Comparable<DateValue>{

    public DateValue(Date date, Float value){
        this.date = date;
        this.value = value;
    }

    public DateValue(){

    }

    public Date date;
    public Float value;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public String toString(){
        return date + " " + value;
    }

    @Override
    public int compareTo(DateValue o) {
        return this.getDate().compareTo(o.getDate());
    }
}


