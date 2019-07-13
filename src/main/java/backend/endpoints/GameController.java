package backend.endpoints;

import backend.endpoints.responses.game.*;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game")
public class GameController {

    @PostMapping("/start")
    ResponseEntity<?> startGame(/*TODO id of game*/){
        StartGameResponse response = new StartGameResponse();

        //TODO set game time in DB, if ok OK, else INTERNAL SERVER ERROR
        return ResponseEntity.ok().body(new Resource<>(response));
    }

    @PostMapping("/stop")
    ResponseEntity<?> stopGame(/*TODO id of game*/){
        StopGameResponse response = new StopGameResponse();

        //TODO delete game from DB, if ok OK, else, INTERNAL SERVER ERROR
        return ResponseEntity.ok().body(new Resource<>(response));
    }

    @PostMapping("/next")
    ResponseEntity<?> nextPlayer(/*TODO id of game*/){
        NextPlayerResponse response = new NextPlayerResponse();

        //TODO try to mark next player as current, if ok OK, else INTERNAL SERVER ERROR
        return ResponseEntity.ok().body(new Resource<>(response));
    }

    @GetMapping("/summary")
    ResponseEntity<?> getSummary(/*TODO id of game*/){
        SummaryResponse response = new SummaryResponse();

        //TODO send response
        return ResponseEntity.ok().body(new Resource<>(response));
    }

    @GetMapping("/synchronize")
    ResponseEntity<?> synchronizeGame(/*TODO id of game*/){
        SynchronizeResponse response = new SynchronizeResponse();

        //TODO create response
        return ResponseEntity.ok().body(new Resource<>(response));
    }
}
