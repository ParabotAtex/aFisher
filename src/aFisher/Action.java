package aFisher;

import org.parabot.environment.api.utils.Time;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.methods.Bank;
import org.rev317.min.api.methods.Inventory;
import org.rev317.min.api.methods.Npcs;
import org.rev317.min.api.methods.Players;
import org.rev317.min.api.methods.Walking;
import org.rev317.min.api.wrappers.Area;
import org.rev317.min.api.wrappers.Tile;
import org.rev317.min.api.wrappers.TilePath;
import org.rev317.min.api.wrappers.Npc;

public class Action implements Strategy {
	int idLobster = 378;
	int idHarpoon =312;
	int idSharkSpot=334;
	int idFishingSpot = 324;
	int idLobsterCage = 302;
	boolean banking=false;
	Tile[] pathToFish = {
			new Tile (2820, 3437),
			new Tile (2834, 3436),
			new Tile (2846, 3431),
			new Tile (2850, 3429),
	};
	Tile[] pathToBank = {
			new Tile (2850, 3429),
			new Tile (2838, 3436),
			new Tile (2823, 3438),
			new Tile (2810, 3440),
	};
	Area fishing = new Area(new Tile(2847, 3430), new Tile(2861, 3430), new Tile(2861, 3420), new Tile(2842, 3420));
	Area bank = new Area(new Tile(2812, 3438), new Tile(2803, 3438), new Tile(2803, 3443), new Tile(2812, 3443));
	@Override
	public boolean activate() {
		if(Players.getMyPlayer().getAnimation() == 619 || Players.getMyPlayer().getAnimation() == 618) {
			return false;
		} else {
			return true;
		}
	}
	@Override
	public void execute() {
		if(fishing.contains(Players.getMyPlayer().getLocation()) && !Inventory.isFull()) {
			Main.status = "Fishing";
			Npc fishingSpot = Npcs.getClosest(idFishingSpot);
			fishingSpot.interact(0);
			banking=false;
			Time.sleep(1000);
		}
		if(Inventory.isFull()) {	
			Main.status = "Banking";
			TilePath pathBank = new TilePath(pathToBank);
			if(!pathBank.hasReached()) {
			      pathBank.traverse();
			      Time.sleep(new SleepCondition() {
			                @Override
			                public boolean isValid() {
			                    return pathBank.hasReached();
			                }
			            }, 3000);

			}
			Bank.open(Bank.getBank());
			Time.sleep(new SleepCondition() {
				@Override
				public boolean isValid() {
					return Bank.isOpen();
				}
			},1000);
			if(Bank.isOpen()) {
				Bank.depositAllExcept(idHarpoon, idLobsterCage);
				Time.sleep(500);
				Bank.close();
			}			
		} 
		if(!fishing.contains(Players.getMyPlayer().getLocation()) && !Inventory.isFull()) {
			Main.status = "Walking to spot";
			TilePath pathSpot = new TilePath(pathToFish);
			if(!pathSpot.hasReached()) {
			      pathSpot.traverse();
			      Time.sleep(new SleepCondition() {
			                @Override
			                public boolean isValid() {
			                    return pathSpot.hasReached();
			                }
			            }, 2500);

			}
		}
	}
}
