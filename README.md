# KiwiClaims
KiwiClaims is a claims plugin for CraftBukkit 1060 (and probably other CraftBukkits). It uses a selection interface similar to that of WorldEdit, and the claims work in a way similar to GriefPrevention.

Note: The main branch of this repository is NOT meant to be used in production. It's for development.

### Building:
To compile KiwiClaims, you will need Apache Maven.

1. Clone this repository.
2. Execute `mvn clean package`

The generated jar file will be in the `target` directory.

### What's done:
 * Claiming
 * Trusting
 * Block protection
 * Storage protection
 * Entity protection
 * Farmland protection
 * Exclusion zones - admin claims whose sole functionality is to stop players from claiming in that area. This is to prevent having other dependencies, like WorldGuard for WorldGuard regions.

### What's to be done:
 * Claim transferring
 * Claims that are configurable as to what things are allowed (e.g. allow button presses from outsiders).
 * Safety mechanism, like GriefPrevention's `/ignoreclaims`, to prevent admins from accidentally causing damage in a claim.
 * Create messages file (e.g. `messages.yml`) so that the messages sent by the plugin can be customised.

Thanks to https://chew.pw/ for hosting the correct Bukkit API in their Jenkins repository.
