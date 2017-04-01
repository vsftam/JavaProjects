package portfolio.model;

import java.util.NoSuchElementException;

/**
 * Created by vincenttam on 3/18/17.
 */
public enum InstrumentType {
    Stock("S"), CallOption("C"), PutOption("P");

    private String _code;

    InstrumentType(String code) {
        _code = code;
    }

    String getCode() { return _code; }

    static public InstrumentType from(String code) {
        InstrumentType ret = null;
        for(InstrumentType e : InstrumentType.values()) {
            if(e.getCode().equals(code))
                ret = e;
        }
        if(ret == null)
            throw new NoSuchElementException("No instrument type for " + code);
        else
            return ret;
    }
}
