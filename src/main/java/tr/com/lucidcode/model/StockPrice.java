package tr.com.lucidcode.model;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by adinema on 15/06/17.
 */
public class StockPrice implements Comparable  {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @NotNull
    private Integer bseId;

    private Float high;
    private Float low;
    private Float open;
    private Float close;

    private Date date;

    private Integer volume;

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBseId() {
        return bseId;
    }

    public void setBseId(Integer bseId) {
        this.bseId = bseId;
    }

    public Float getHigh() {
        return high;
    }

    public void setHigh(Float high) {
        this.high = high;
    }

    public Float getLow() {
        return low;
    }

    public void setLow(Float low) {
        this.low = low;
    }

    public Float getOpen() {
        return open;
    }

    public void setOpen(Float open) {
        this.open = open;
    }

    public Float getClose() {
        return close;
    }

    public void setClose(Float close) {
        this.close = close;
    }

    public String toString(){
        return bseId + " " + open + " " + close;
    }

    @Override
    public int compareTo(Object o) {
        Date oDate = (Date)o;
        return date.compareTo(oDate);
    }
}
