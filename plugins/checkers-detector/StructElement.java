public class StructElement {
    public enum Type {
        SQUARE,
        DIAMOND,
        CIRCLE,
        EMPTY
    }

    private int[] _data;
    private Type _type;
    private int _radius;
    private int _width;

    public StructElement(Type type, int radius) {
        _radius = radius;
        _width = 2 * _radius + 1;
        _type = type;
        _data = new int[_width * _width];

        switch (_type) {
        case SQUARE:
            _initSquare();
            break;
        case DIAMOND:
            _initDiamond();
            break;
        case CIRCLE:
            _initCircle();
            break;
        case EMPTY:
            break;
        default:
            System.out.println("Structuring element error: Invalid type");
        }
    }

    private void _initSquare() {
        for (int i = 0; i < _data.length; i++)
            _data[i] = 1;
    }

    private void _initDiamond() {
        for (int y = -_radius; y <= _radius; y++) {
            for (int x = -_radius; x <= _radius; x++) {
                if (Math.abs(x) + Math.abs(y) <= _radius)
                    put(x, y, 1);
            }
        }
    }

    private void _initCircle() {
        for (int y = -_radius; y <= _radius; y++) {
            for (int x = -_radius; x <= _radius; x++) {
                if (Math.round(Math.hypot(x, y)) <= _radius)
                    put(x, y, 1);
            }
        }
    }

    public int getRadius() {
        return (_radius);
    }

    public int get(int x, int y) {
        return (_data[(y + _radius) * _width + (x + _radius)]);
    }

    public void put(int x, int y, int val) {
        _data[(y + _radius) * _width + (x + _radius)] = val;
    }

    public void print() {
        for (int y = -_radius; y <= _radius; y++) {
            for (int x = -_radius; x <= _radius; x++) {
                if (get(x, y) == 1)
                    System.out.print("XX");
                else
                    System.out.print("  ");
            }
            System.out.println("");
        }
    }
}
