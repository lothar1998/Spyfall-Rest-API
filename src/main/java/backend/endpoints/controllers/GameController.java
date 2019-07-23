package backend.endpoints.controllers;

import backend.endpoints.ContextPaths;
import backend.endpoints.responses.game.*;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @PostMapping(ContextPaths.GAME_START)
    ResponseEntity<?> startGame(/*TODO id of game*/){
        StartGameResponse response = new StartGameResponse();

        //TODO set game time in DB, if ok OK, else INTERNAL SERVER ERROR
        return ResponseEntity.ok().body(new Resource<>(response));
    }

    @PostMapping(ContextPaths.GAME_STOP)
    ResponseEntity<?> stopGame(/*TODO id of game*/){
        StopGameResponse response = new StopGameResponse();

        //TODO delete game from DB, if ok OK, else, INTERNAL SERVER ERROR
        return ResponseEntity.ok().body(new Resource<>(response));
    }

    @PostMapping(ContextPaths.GAME_NEXT_PLAYER)
    ResponseEntity<?> nextPlayer(/*TODO id of game*/){
        NextPlayerResponse response = new NextPlayerResponse();

        //TODO try to mark next player as current, if ok OK, else INTERNAL SERVER ERROR
        return ResponseEntity.ok().body(new Resource<>(response));
    }

    @GetMapping(ContextPaths.GAME_SUMMARY)
    ResponseEntity<?> getSummary(/*TODO id of game*/){
        SummaryResponse response = new SummaryResponse();

        //TODO send response
        return ResponseEntity.ok().body(new Resource<>(response));
    }

    @GetMapping(ContextPaths.GAME_SYNCHRONIZE)
    ResponseEntity<?> synchronizeGame(/*TODO id of game*/){
        SynchronizeResponse response = new SynchronizeResponse();

        //TODO create response
        return ResponseEntity.ok().body(new Resource<>(response));
    }
}
