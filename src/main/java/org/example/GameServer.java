package org.example;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameServer extends Remote {

    void updatePlayer(PlayerState player) throws RemoteException;

    List<PlayerState> getPlayers() throws RemoteException;

    void shoot(Bullet bullet) throws RemoteException;

    List<Bullet> getBullets() throws RemoteException;
}