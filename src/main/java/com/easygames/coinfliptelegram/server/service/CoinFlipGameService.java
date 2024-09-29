//package com.easygames.coinfliptelegram.service;
//
//import com.easygames.coinfliptelegram.dao.PlayedGameRepository;
//import com.easygames.coinfliptelegram.dao.UserRepository;
//import com.easygames.coinfliptelegram.model.*;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class CoinFlipGameService {
//
//    private final UserRepository userRepository;
//    private final PlayedGameRepository playedGameRepository;
//
//    public CoinFlipGameService(UserRepository userRepository, PlayedGameRepository finishedGameRepository) {
//        this.userRepository = userRepository;
//        this.playedGameRepository = finishedGameRepository;
//    }
//
//    private final Map<String, GameInfo> games = new HashMap<>();
//    private final Map<Long, Set<String>> playerGames = new HashMap<>();
//    private final Map<String, MessageInfo> originalMessages = new HashMap<>();
//
//    public User getOrCreateUser(Long telegramId, String username) {
//        User user = userRepository.findByTelegramId(telegramId);
//        if (user == null) {
//            user = new User();
//            user.setTelegramId(telegramId);
//            user.setUsername(username);
//            user.setScore(0);
//            user.setFlipky(100); // Starting amount of flipky
//            userRepository.save(user);
//        }
//        return user;
//    }
//
//    public void saveFinishedGame(String gameId, Long initiatorId, Long opponentId, int betAmount, String initiatorChoice, String opponentChoice, String result) {
//        Game playedGame = new Game();
//        playedGame.setGameId(gameId);
//        playedGame.setInitiatorId(initiatorId);
//        playedGame.setOpponentId(opponentId);
//        playedGame.setBetAmount(betAmount);
//        playedGame.setInitiatorChoice(initiatorChoice);
//        playedGame.setOpponentChoice(opponentChoice);
//        playedGame.setResult(result);
//        playedGameRepository.save(playedGame);
//    }
//
//
//
//
//    public GameInfo getGameWaitingBet(long playerId) {
//        List<GameInfo> currentGames = games.values().stream().filter(gameInfo -> GameState.ENTERING_CUSTOM_BET.equals(gameInfo.getState()) && playerId == gameInfo.getInitiatorId()).toList();
//
//        return !currentGames.isEmpty() ? currentGames.get(0) : null;
//    }
//
//    public boolean canGenerateNewGame(long playerId) {
//        return true;
//    }
//
//    public JoinGameResult joinGame(long playerId, String gameCode, String username) {
//        GameInfo game = games.get(gameCode);
//        if (game == null) {
//            return new JoinGameResult(false, "Invalid game code or the game has already finished.", -1);
//        }
//        if (game.getInitiatorId() == playerId) {
//            return new JoinGameResult(false, "You can't play against yourself.", -1);
//        }
//        if (game.getState() != GameState.WAITING_FOR_OPPONENT) {
//            return new JoinGameResult(false, "This game is no longer available.", -1);
//        }
//
//        synchronized (game) {
//            if (game.getState() != GameState.WAITING_FOR_OPPONENT) {
//                return new JoinGameResult(false, "Sorry, someone else has already joined this game.", -1);
//            }
//            game.setOpponentId(playerId);
//            game.setOpponentUsername(username);
//            game.setState(GameState.IN_PROGRESS);
//        }
//        getOrCreateUser(game.getInitiatorId(), game.getInitiatorUsername());
//        getOrCreateUser(playerId, username);
//
//        playerGames.computeIfAbsent(playerId, k -> new HashSet<>()).add(gameCode);
//
//        return new JoinGameResult(true, game.getInitiatorUsername(), game.getInitiatorId());
//    }
//
//    public boolean isGameJoinable(String gameCode) {
//        GameInfo game = games.get(gameCode);
//        return game != null && game.getState() == GameState.WAITING_FOR_OPPONENT;
//    }
//
//    public boolean canMakeChoice(long playerId, String gameCode) {
//        GameInfo game = games.get(gameCode);
//        return game != null && game.getState() == GameState.IN_PROGRESS &&
//                (game.getInitiatorId() == playerId || game.getOpponentId() == playerId);
//    }
//
//    public GameResult makeChoice(long playerId, String gameCode, String choice) {
//        GameInfo game = games.get(gameCode);
//        if (game == null || game.getState() != GameState.IN_PROGRESS) {
//            return null;
//        }
//
//        if (game.getInitiatorId() == playerId) {
//            game.setInitiatorChoice(choice);
//        } else if (game.getOpponentId() == playerId) {
//            // can extract opponent choosing logic
//        } else {
//            return null; // Player is neither initiator nor opponent
//        }
//
//        boolean isHeads = Math.random() < 0.5;
//        String result = isHeads ? "Heads" : "Tails";
//        boolean playerWins = (isHeads && choice.equals("heads")) || (!isHeads && choice.equals("tails"));
//
//        int betAmount = game.getBetAmount(); // You can make this dynamic based on user input
//
//        // Update scores and flipky for both players
//        updateUserScoreAndFlipky(game.getInitiatorId(), playerWins ? 1 : -1, playerWins ? betAmount : -betAmount);
//        updateUserScoreAndFlipky(game.getOpponentId(), playerWins ? -1 : 1, playerWins ? -betAmount : betAmount);
//
//        // Save the finished game
//        saveFinishedGame(gameCode, game.getInitiatorId(), game.getOpponentId(), betAmount,
//                game.getInitiatorChoice(), choice, result);
//
//        game.setState(GameState.FINISHED);
//        playerGames.get(game.getInitiatorId()).remove(gameCode);
//        playerGames.get(game.getOpponentId()).remove(gameCode);
//        games.remove(gameCode);
//
//        long opponentId = (game.getInitiatorId() == playerId) ? game.getOpponentId() : game.getInitiatorId();
//        return new GameResult(result, choice, playerWins, opponentId);
//    }
//
//    private void updateUserScoreAndFlipky(long userId, int scoreChange, int flipkyChange) {
//        User user = userRepository.findByTelegramId(userId);
//        if (user != null) {
//            user.setScore(user.getScore() + scoreChange);
//            user.setFlipky(user.getFlipky() + flipkyChange);
//            userRepository.save(user);
//        }
//    }
//
//    public List<String> getActiveGames(long playerId) {
//        return new ArrayList<>(playerGames.getOrDefault(playerId, Collections.emptySet()));
//    }
//
//    public void storeOriginalMessage(String gameCode, long chatId, int messageId) {
//        MessageInfo messageInfo = originalMessages.get(gameCode);
//        if (messageInfo == null) {
//            Set<Long> chatIds = new HashSet<>();
//            chatIds.add(chatId);
//            originalMessages.put(gameCode, new MessageInfo(chatIds, messageId));
//        } else {
//            messageInfo.getChatIds().add(chatId);
//            originalMessages.put(gameCode, messageInfo);
//        }
//    }
//
//    public Set<Long> getOriginalChatId(String gameCode) {
//        MessageInfo info = originalMessages.get(gameCode);
//        return info != null ? info.getChatIds() : null;
//    }
//
//    public int getOriginalMessageId(String gameCode) {
//        MessageInfo info = originalMessages.get(gameCode);
//        return info != null ? info.getMessageId() : -1;
//    }
//
//    public boolean cancelGame(long playerId, String gameCode) {
//        GameInfo game = games.get(gameCode);
//        if (game != null && game.getState() == GameState.WAITING_FOR_OPPONENT && game.getInitiatorId() == playerId) {
//            games.remove(gameCode);
//            playerGames.get(playerId).remove(gameCode);
//            return true;
//        }
//        return false;
//    }
//
//
//    public boolean isGameFinished(String gameCode) {
//        GameInfo game = games.get(gameCode);
//        return game == null || game.getState() == GameState.FINISHED;
//    }
//
//    public void removeGame(String gameCode) {
//        games.remove(gameCode);
//        originalMessages.remove(gameCode);
//        // Remove from playerGames as well
//    }
//
//    public void finishGame(String gameCode) {
//        GameInfo game = games.remove(gameCode);
//        if (game != null) {
//            playerGames.get(game.getInitiatorId()).remove(gameCode);
//            if (game.getOpponentId() != null) {
//                playerGames.get(game.getOpponentId()).remove(gameCode);
//            }
//        }
//    }
//
//    public void cleanupFinishedGames() {
//        games.entrySet().removeIf(entry -> {
//            GameInfo game = entry.getValue();
//            if (game.getState() == GameState.FINISHED) {
//                playerGames.getOrDefault(game.getInitiatorId(), Collections.emptySet()).remove(entry.getKey());
//                if (game.getOpponentId() != null) {
//                    playerGames.getOrDefault(game.getOpponentId(), Collections.emptySet()).remove(entry.getKey());
//                }
//                return true;
//            }
//            return false;
//        });
//    }
//
//    public void setBetAmount(GameInfo game, int betAmount) {
//        game.setBetAmount(betAmount);
//        games.put(game.getGameCode(), game);
//    }
//
//    public void changeGameState(GameInfo game, GameState gameState) {
//        game.setState(gameState);
//        games.put(game.getGameCode(), game);
//    }
//
//    public void changeGameState(String gamecode, GameState gameState) {
//        games.get(gamecode).setState(gameState);
//    }
//
//    public GameInfo getGame(String gameCode) {
//        return games.get(gameCode);
//    }
//}