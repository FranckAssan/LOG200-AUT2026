
enum Mark {
    X("X"),
    O("O"),
    EMPTY("");

    private final String mark;

    Mark(String mark) {this.mark = mark;}

    @Override
    public String toString() {
        return this.mark;
    }
}

