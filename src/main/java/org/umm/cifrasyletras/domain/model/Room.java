package org.umm.cifrasyletras.domain.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    private String id;
    private User owner;
    private List<User> players;
    private boolean isPrivate;
    private boolean isGameStarted;

    public Room(String id, User owner, boolean isPrivate) {
        this.id = id;
        this.owner = owner;
        this.players = new ArrayList<>(List.of(owner));
        this.isPrivate = isPrivate;
        this.isGameStarted = false;
    }

    public boolean isFull() {
        return players.size() >= 2;
    }

    public void addPlayer(User player) {
        if (!isFull()) {
            players.add(player);
        }
    }

    public void removePlayer(User player) {
        players.remove(player);
        if (!players.contains(owner) && !players.isEmpty()) {
            owner = players.get(0);
        }
    }
}
