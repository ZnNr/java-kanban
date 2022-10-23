package status;

public enum Status {
    NEW("НОВАЯ"),
    IN_PROGRESS("В ПРОЦЕССЕ"),
    DONE("ЗАКОНЧЕНА");


    private String translation;

    Status() {
    }

    Status(String translation) {
        this.translation = translation;
    }


    public String toString() {
        return translation;
    }


}