# KiwiClaims
KiwiClaims is a claims plugin for CraftBukkit 1060. The interface is inspired by those of WorldEdit and GriefPrevention. I hope to turn this into a fully featured plugin that can compete with modern claims plugins.

Note: This plugin is currently NOT meant to be used in production. There WILL be issues.

### What's done:
 * Claiming
 * Trusting
 * Block protection
 * Entity protection
 * Farmland protection
 * Exclusion zones - admin claims whose sole functionality is to stop players from claiming in that area. This is to prevent having other dependencies, like WorldGuard for WorldGuard regions.

### What's to be done:
 * Claim transferring
 * Claims that are configurable as to what things are allowed (e.g. allow button presses from outsiders).
 * Safety mechanism, like GriefPrevention's `/ignoreclaims`, to prevent admins from accidentally causing damage in a claim.

Thanks to https://chew.pw/ for hosting the correct Bukkit API in their Jenkins repository.
