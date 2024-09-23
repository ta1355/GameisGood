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
    private final ObjectMapper objectMapper;

    public GameService(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    public GameDto find() {
        try {
            String xml = webClient
                    .get()
                    .uri("https://www.grac.or.kr/WebService/GameSearchSvc.asmx/game?display=10&pageno=1")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (xml == null || xml.isEmpty()) {
                throw new RuntimeException("Received empty XML response");
            }

            // XML을 GameDto로 변환
            JAXBContext jaxbContext = JAXBContext.newInstance(GameDto.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            GameDto gameResponse = (GameDto) unmarshaller.unmarshal(new StringReader(xml));

            // GameDto를 JSON으로 변환
            String jsonString = objectMapper.writeValueAsString(gameResponse);

            // JSON 문자열을 다시 GameDto로 변환
            return objectMapper.readValue(jsonString, GameDto.class);

        } catch (Exception e) {
            throw new RuntimeException("Error occurred while retrieving game data", e);
        }
    }
}

