package bsu.rpact.medionefrontend.pojo.other;

import java.util.List;

public class RestCountry {

    public class ComplexName {

        private String official;

        public ComplexName(String official) {
            this.official = official;
        }

        public ComplexName() {
        }

        public String getOfficial() {
            return official;
        }

        public void setOfficial(String official) {
            this.official = official;
        }
    }

    public class Flags {

        private String png;

        public Flags(String png) {
            this.png = png;
        }

        public Flags() {
        }

        public String getPng() {
            return png;
        }

        public void setPng(String png) {
            this.png = png;
        }
    }

    public ComplexName name;
    public Flags flags;

    public RestCountry(ComplexName name, Flags flags) {
        this.name = name;
        this.flags = flags;
    }

    public RestCountry() {
    }

    public ComplexName getName() {
        return name;
    }

    public void setName(ComplexName name) {
        this.name = name;
    }

    public Flags getFlags() {
        return flags;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }
}
