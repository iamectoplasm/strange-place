package configs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import game.EntityFactory.EntityName;

public class MapConfig
{
	private static final String TAG = MapConfig.class.getSimpleName();
	
	private Vector2 generalPlayerStart;
	private Array<MapEntity> npcs;

	public MapConfig()
	{
		Gdx.app.debug(TAG, "MapConfig object constructed");
		
		this.generalPlayerStart = new Vector2();
		this.npcs = new Array<MapEntity>();
	}
	
	public Vector2 getGeneralPlayerStart()
	{
		return generalPlayerStart;
	}

	public void setGeneralPlayerStart(Vector2 generalPlayerStart)
	{
		this.generalPlayerStart = generalPlayerStart;
	}
	
	public Array<MapEntity> getNpcs()
	{
		return npcs;
	}

	public void setNpcs(Array<MapEntity> npcs)
	{
		this.npcs = npcs;
	}
	
	public static class MapEntity
	{
		private EntityName npc;
		private Vector2 npcStart;
		
		public MapEntity()
		{
			npc = EntityName.SYLVANA;
			npcStart = new Vector2();
		}
		
		public EntityName getNpc() {
			return npc;
		}

		public void setNpc(EntityName npc) {
			this.npc = npc;
		}

		public Vector2 getNpcStart() {
			return npcStart;
		}

		public void setNpcStart(Vector2 npcStart) {
			this.npcStart = npcStart;
		}
	}
}
