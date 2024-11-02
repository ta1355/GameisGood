package game.gamegoodgood.game;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

@Service
public class GameService {

    private final WebClient webClient;

    public GameService(WebClient webClient) {
        this.webClient = webClient;
    }

    public GameDto find(String display, String pageNo, String gametitle, String entname, String rateno) {

        String uri = String.format(
                "https://www.grac.or.kr/WebService/GameSearchSvc.asmx/game?display=%s&pageno=%s&gametitle=%s&entname=%s&rateno=%s",
                display, pageNo, gametitle, entname, rateno
        );

        try {
            String xml = webClient
                    .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)   //xml을 받기 위해 bodyToMono를 사용
                    .block(); //동기화 작업

            if (xml == null || xml.isEmpty()) {
                throw new RuntimeException("데이터가 없습니다. 다시 한번 확인 부탁합니다.");
            }

            JAXBContext jaxbContext = JAXBContext.newInstance(GameDto.class);  //xml를 자바 객체로 변환하는 기능을 담당
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller(); // 실제로 xml 데이터를 읽어 자바로 변환, 변환을 위한 설정 포함, unmarshaller 는 여러번 호출 가능
            GameDto gameResponse = (GameDto) unmarshaller.unmarshal(new StringReader(xml));  //xml를 읽을 수 있는 형식으로 변환

            return gameResponse;
        } catch (Exception e) {
            throw new RuntimeException("런타임 에러 다시 확인 바랍니다.", e);
        }
    }
}
