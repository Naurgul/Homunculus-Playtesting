package genetics;

import tactical.GameState;

public interface PlayStyle
{
	public Class<?> getStateClass();
	public double distance(PlayStyle other);
}