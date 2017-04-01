package portfolio.model;

/**
 * Created by vincenttam on 3/18/17.
 */
public class Position {

    private Instrument _instrument;
    private int _quantity;
    private boolean _longPosition;

    public Position(Instrument instrument, int quantity, boolean longPosition) {
        _instrument = instrument;
        _quantity = quantity;
        _longPosition = longPosition;
    }

    public double marketValue() {
        return _instrument.price() * _quantity * (_longPosition ? 1 : -1);
    }

    public Instrument getInstrument() { return _instrument; }

    public String reportString() {
        String longShort = _longPosition ? "Long " : "Short ";
        return longShort +"\t" + _quantity + "\t" + _instrument.reportString();
    }

    @Override
    public String toString() {
        String longShort = _longPosition ? "Long " : "Short ";
        return longShort + _quantity + " on "+ _instrument.toString();
    }
}
