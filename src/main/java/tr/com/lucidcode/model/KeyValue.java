package tr.com.lucidcode.model;

/**
 * Created by adinema on 11/06/17.
 */
public class KeyValue {

    public String key;
    public Object value;
    private Integer order;

    public KeyValue(String key, Object value, Integer order){
        this.key = key;
        this.value = value;
        this.order = order;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public String toString(){
        return key + " " + value;
    }
}
