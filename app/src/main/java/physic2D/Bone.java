package physic2D;

public class Bone extends Actor {
    private boolean isActive = true;

    @Override
    public void collideWithActor(Actor actor) {
        if (isActive && actor instanceof Player) {
            this.isActive = false;
            Player player = (Player) actor;
            player.win();
        }
    }
}