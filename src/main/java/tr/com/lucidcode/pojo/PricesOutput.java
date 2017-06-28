package tr.com.lucidcode.pojo;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by adinema on 19/06/17.
 */
public class PricesOutput {


    private Integer bseId;

    private Float high;
    private Float low;
    private Float open;
    private Float close;

    private Date priceDate;

    private Integer volume;

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

    public Date getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(Date priceDate) {

        this.priceDate = priceDate;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }
}
