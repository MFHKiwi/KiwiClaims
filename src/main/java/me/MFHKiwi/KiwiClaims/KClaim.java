package me.MFHKiwi.KiwiClaims;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class KClaim {
	public final int x1, z1, x2, z2;
	public final Player owner;
	public final World world;
	
	public KClaim(int x1, int z1, int x2, int z2, Player owner, World world) {
		this.x1 = x1;
		this.z1 = z1;
		this.x2 = x2;
		this.z2 = z2;
		this.owner = owner;
		this.world = world;
	}
	
	public boolean contains(Block block) {
		boolean xwithin = block.getX() >= this.x1 && block.getX() <= this.x2? true : false;
		boolean zwithin = block.getZ() >= this.z1 && block.getZ() <= this.z2? true : false;
		if (xwithin && zwithin) {
			return true;
		} else return false;
	}
}
