package org.mousephenotype.dcc.utils.io.conf;

public class StringReaderCommand implements Command<String> {

    public enum commentChars {

        DASH("-"), HASH("#"), SLASH("/");
        private String character;

        private commentChars(String character) {
            this.character = character;
        }
        public String character() {
        return character;
    }
    }

    private boolean isComment(String line) {
        for (commentChars c : commentChars.values()) {
            if(line.trim().startsWith(c.character())){
                return true;
            }
        }
        return false;
    }

    @Override
    public String execute(String line) {
        if (!line.isEmpty() && !this.isComment(line.trim())) {
            return line;
        }
        return null;
    }
}
