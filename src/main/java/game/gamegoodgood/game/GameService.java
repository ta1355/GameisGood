package game.gamegoodgood.game;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

@Service
public class GameService {

    private final WebClient webClient;

    public GameService(WebClient webClient) {
        this.webClient = webClient;
    }

    public GameDto find(String display, String pageNo, String gametitle, String entname, String rateno) {
        // URL 생성
        String uri = String.format(
                "https://www.grac.or.kr/WebService/GameSearchSvc.asmx/game?display=%s&pageno=%s&gametitle=%s&entname=%s&rateno=%s",
                display, pageNo, gametitle, entname, rateno
        );

        try {
            String xml = webClient
                    .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (xml == null || xml.isEmpty()) {
                throw new RuntimeException("Received empty XML response");
            }

            JAXBContext jaxbContext = JAXBContext.newInstance(GameDto.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            GameDto gameResponse = (GameDto) unmarshaller.unmarshal(new StringReader(xml));

            return gameResponse;
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while retrieving game data", e);
        }
    }
}
