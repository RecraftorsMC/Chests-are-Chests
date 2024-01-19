Ever wondered why, when a chest is open, items don't fall in?<br>
... Okay, I'll admit, me neither, not until I had this idea. But when you think of it...
It just makes sense, right?

So now, you can proudly say that **Chests are chests**! And enjoy making items fall into your storage
instead of lamely putting them in by hand!

But as items can fall *into* an open box... Shouldn't they be able to fall *down* from one as well,
provided it was open on the right side?
Thus, you can make it so barrels, when open and facing down, drop *all* of their content to the ground!

Going back to the chest, if you can open the lid to make items fall in, shouldn't the lid throw items away
upon being opened?
That's right! With this mod, you can make it so opening a chest throws all the items standing on it upon opening!
(Limited to items only for the sake of one's life preservation)

Last but not least, since opening a container has always only been a manual task so far,
in order to allow you to automate these new options, opening a container can now be done
by activating an *empty* dispenser pointing at said container!

Now the game will feel more natural, even just a little bit, won't you agree?

## Features

* Inserting items in open containers
  * default: enabled
  * gamerule: `chests.insertOpen`
* Making item fall from open-from-below containers
  * default: disabled
  * gamerule: `chests.barrelFall`
* Making chest lids throw items
  * default: disabled
  * gamerule: `chests.lidFling`
  * Horizontal flinging velocity
    * default: 0.25
    * gamerule: `chests.lidFling.horizontalPower`
  * Vertical flinging velocity
    * default: 0.6
    * gamerule: `chests.lidFling.verticalPower`
* Opening containers by powering empty dispensers
  * default: enabled
  * gamerule: `chests.dispenserOpen`
  * Automated opening duration (in world ticks)
    * default: 10
    * gamerule: `chests.dispenserOpen.duration`

## Mod compatibilities

### Internal compatibilities
While compatibilities are planned, direct implementation by the mods themselves would always be preferred.
Hence, if a mod of yours is [planned](#planned), [being worked on](#being-worked-on), or even already implemented,
don't hesitate to [make your own](#make-your-own), and if need be, contact us either for help or to tell us
that we have no need to keep our own compatibility.

**Implemented**
* [Anner's Iron Chests](https://modrinth.com/mod/cyberanner-ironchest)
* [Mythic Metals Decorations](https://modrinth.com/mod/mythicmetals-decorations)
* [Reinforced Chests](https://modrinth.com/mod/reinforced-chests)

#### Being worked on
* [Lootr](https://modrinth.com/mod/lootr)
* [Spectrum](https://modrinth.com/mod/spectrum)

#### Planned
* [Iron Chests: Restocked](https://modrinth.com/mod/ironchests)
* [myLoot](https://modrinth.com/mod/myloot)
* [More Chests](https://modrinth.com/mod/more-chests)
* [More Chests Variants](https://modrinth.com/mod/more-chest-variants-lieonlion)
* [Compact Storage](https://modrinth.com/mod/compact_storage)

### Make your own

The mod tweaks the block entities' and item entities' behaviours.
In order to implement compatibility with this mod, you need your custom block entity to implement
the `mc.recraftors.chestsarechests.util.FallInContainer` interface.

You may as well make use of other interfaces, such as `mc.recraftors.chestsarechests.util.BlockOpenableContainer`
or `mc.recraftors.chestsarechests.util.BooleanHolder`, which you can either use as you please, or can exploit
if you already extend the vanilla chest or barrel classes.

Don't hesitate to look at the
[chest](./common/src/main/java/mc/recraftors/chestsarechests/mixin/block_entities/ChestBlockEntityMixin.java)
and [barrel](./common/src/main/java/mc/recraftors/chestsarechests/mixin/block_entities/BarrelBlockEntityMixin.java)
implementations for a reference of how to handle block opening and orientation within the provided methods.

Also don't fear about more methods appearing with updates and your mod not implementing them, causing issues.
No methods are planned for deletion, or would include a change of primary version. And all new method
will _always_ come with a default implementation, to avoid pointless issues.

#### Example
Up to date with version **0.5**

```java
public class MyCustomBlockEntity extends BlockEntity implements FallInContainer {
    private boolean isOpen;
    private final int size;
    private final DefaultedList<ItemStack> content;
    private final Map<Integer, Integer> fallUpdateMap = new HashMap<>();

    @Override
    public boolean chests$tryInsertion(ItemEntity entity) {
        return FallInContainer.chests$inventoryInsertion(this.content, entity, this::setStack);
    }

    @Override
    public boolean chests$isOpen() {
        return isOpen;
    }

    @Override
    public VoxelShape chests$InputAreaShape() {
        return FallInContainer.ABOVE;
    }

    @Override
    public void chests$forceOpen(ServerWorld world, BlockPos at, BlockState from) {
        this.isOpen = true;
        this.onOpen();
    }

    @Override
    public boolean chests$tryForceOpen(BlockState from) {
        ServerWorld serverWorld = (ServerWorld) this.getWorld();
        BlockPos blockPos = this.getPos();
        this.chests$forceOpen(serverWorld, blockPos, from);
        ChestsAreChests.scheduleTick(serverWorld, blockPos, duration);
    }

    @Override
    public boolean chests$forceClose() {
        this.isOpen = false;
        this.onClose();
    }

    @Override
    public Map<Integer, Integer> getFallUpdateMap() {
        return this.fallUpdateMap;
    }

    void onOpen() {
        // ...
        // potential rendering stuff
        ChestsAreChests.ejectAbove(Direction.UP, this);
    }
}

```

## Showcase

A (silent) showcase of this mod can be found [here](https://youtu.be/YYWvP2HPn34)