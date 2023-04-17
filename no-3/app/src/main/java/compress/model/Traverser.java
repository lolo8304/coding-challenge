package compress.model;

public class Traverser {

    private String encoding;
    private int index;
    private int current;
    private StringBuilder buffer;
    private HuffNode root;
    private HuffNode currentRoot;

    public Traverser(String encoding, HuffNode root) {
        this.root = root;
        this.currentRoot = root;
        this.buffer = new StringBuilder();
        this.encoding = encoding;
        this.index = 0;
        this.current = encoding.charAt(this.index);
    }

    private int next() {
        if (this.index < this.encoding.length()-1) {
            this.index++;
            this.current = this.encoding.charAt(this.index);
        } else {
            this.current = -1;
        }
        return this.current;
    }
    private int resetAndNext() {
        this.currentRoot = this.root;
        return this.next();
    }

    private boolean isStart() {
        return this.currentRoot == this.root;
    }
    private boolean isFinished() {
        // bins are filled up with 0 and every code should start with '1'
        return (this.current == -1) || (isStart() && this.current == '0');
    }
    public String decode() {
        while (!isFinished()) {
            if (this.isStart()) {
                this.next();
            }
            if (this.currentRoot.isLeaf()) {
                buffer.append(this.currentRoot.valueString());
                this.currentRoot = this.root;
            } else {
                if (this.current == '0') {
                    this.currentRoot = this.currentRoot.left();
                } else {
                    this.currentRoot = this.currentRoot.right();
                }
                next();
            }    
        }
        if (this.currentRoot.isLeaf()) {
            buffer.append(this.currentRoot.valueString());
        }
        return buffer.toString();
    }

    private String encodingExcept() {
        var buffer = new StringBuilder();
        for (int i = 0; i < this.encoding.length(); i++) {
            if (i == this.index) {
                buffer.append("_");
            } else {
                buffer.append(this.encoding.charAt(i));
            }            
        }
        return buffer.toString();
    }

    @Override
    public String toString() {
        return "Traverser [encoding=" + encoding + "/"+this.encodingExcept()+"]";
    }

    
    
}
