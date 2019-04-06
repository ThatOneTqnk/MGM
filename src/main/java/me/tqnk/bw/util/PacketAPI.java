package me.tqnk.bw.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.tqnk.bw.MGM;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class PacketAPI {
    public static void sendTablistHeaderFooter(Player p, String header, String footer) {
        ProtocolManager pmref = MGM.get().getProtocolManager();
        PacketContainer modifiedTablist = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        modifiedTablist.getModifier().writeDefaults();
        modifiedTablist.getChatComponents().write(0, WrappedChatComponent.fromText(header)).write(1, WrappedChatComponent.fromText(footer));
        try {
            pmref.sendServerPacket(p, modifiedTablist);
        } catch (InvocationTargetException e) {}
    }
}
