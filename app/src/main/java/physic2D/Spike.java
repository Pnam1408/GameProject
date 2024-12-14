package physic2D;

public class Spike extends Actor {
    private boolean isActive = true;

    @Override
    public void collideWithActor(Actor actor) {
        if (isActive && actor instanceof Player) {
            isActive = false;
            Player player = (Player) actor;
            player.die();
        }
    }
}
