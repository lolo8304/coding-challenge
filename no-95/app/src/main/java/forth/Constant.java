package forth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class Constant extends Variable {
    public Constant(Integer address, Integer length) {
        super(address, length);
    }
}
