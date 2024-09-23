package game.gamegoodgood.game;

import lombok.Data;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class GameDto {

        @XmlElement(name = "item")
        private List<Item> items;

        @XmlElement(name = "tcount")
        private int totalCount;

        @XmlElement(name = "pageno")
        private int pageNo;

        @XmlElement(name = "res_date")
        private String responseDate;

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Item {
                @XmlElement(name = "rateno")
                private String rateNo;

                @XmlElement(name = "rateddate")
                private String ratedDate;

                @XmlElement(name = "gametitle")
                private String gameTitle;

                @XmlElement(name = "orgname")
                private String orgName;

                @XmlElement(name = "entname")
                private String entName;

                @XmlElement(name = "summary")
                private String summary;

                @XmlElement(name = "givenrate")
                private String givenRate;

                @XmlElement(name = "genre")
                private String genre;

                @XmlElement(name = "platform")
                private String platform;

                @XmlElement(name = "descriptors")
                private String descriptors;

                @XmlElement(name = "cancelstatus")
                private boolean cancelStatus;

                @XmlElement(name = "canceleddate")
                private String canceledDate;
        }
}
