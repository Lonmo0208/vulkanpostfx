# VulkanPostFX

An experimental **external ZIP shader pack loader** and **early-stage shader translation framework** for the modern Minecraft Vulkan rendering path.

The current goal is not to fully replicate Iris / OptiFine pipeline compatibility in one step, but to first establish:

- external ZIP shader pack discovery
- runtime resource injection
- post-effect entry loading
- initial shader translation based on external packs

---

## Current Progress

The project already supports the following core features:

- scanning external ZIP shader packs under `run/shaderpacks/`
- reading and parsing `pack.json` inside shader packs
- explicitly selecting the active pack via `active_pack_id`
- materializing external ZIP packs into runtime resource packs
- injecting external `post_effect/main.json` into the game resource pipeline
- actually loading external post chains through `ShaderManager`
- an **initial translated wrapper** of **Verdant Light 1.3.0**

This means the project is no longer at the stage of merely “reading ZIP files” or “pretending to load them.” It already implements:

**external ZIP shader pack -> runtime resource pack -> external post chain execution**

---

## Project Positioning

### What this is
This is an experimental shader pack loader for the **modern Minecraft Vulkan backend**.

### What this is not
This is **not** yet a mature drop-in replacement for Iris / OptiFine with full shader semantic compatibility.

A more accurate description of the current state would be:

- a **ZIP shader pack loader prototype**
- a **post-effect-driven shader translator**
- a **working proof of concept for the Vulkan path**

---

## Implemented Features

### 1. External Pack Loading
Supports loading the following from ZIP shader packs:

- `pack.json`
- `post_effect/main.json`
- `shaders/post/*.vsh`
- `shaders/post/*.fsh`

### 2. Runtime Namespace Rewriting
External packs are rewritten into isolated runtime namespaces to avoid conflicts with built-in resources.

### 3. Runtime Resource Pack Injection
External ZIP contents are materialized into runtime resource packs during startup / resource reload and injected into the client resource system.

### 4. External Post Chain Execution
The project can now confirm that external post chains are actually found and executed, rather than simply falling back to built-in debug effects.

### 5. Initial Shader Porting
It is already possible to extract and translate the **final look layer** from traditional shader packs into a runnable Vulkan post-effect version.

---

## Current Limitations

The current version still has clear boundaries. Please keep them in mind before use.

### Not fully supported yet
The following traditional shader pipeline components are not fully supported at this stage:

- `gbuffers_*`
- `shadow.*`
- `composite.*`
- screen-space reflections (SSR)
- volumetric fog
- depth-driven material separation
- full dimension-specific shader routing (such as `world1/`, `world-1/`)

### Currently suitable for porting
The most suitable parts to port right now are:

- `final.fsh`
- final grading
- vignette
- highlight compression
- tonemapping
- screen-space look layers

### Known Issues
- Some translated packs still have **brightness mismatch**
- Some color tones may still deviate from the original pack’s intended style
- runtime resource pack metadata is still being refined and standardized
- not every original shader pack can simply be used as-is

---

## Current Available Sample

### Verdant Light 1.3.0 Vulkan Wrapper
The repository already includes an **initial Vulkan-translated version** based on **Verdant Light 1.3.0**.

This is not a full lossless recreation of the original shader pack. Instead, it:

- keeps the original pack resources as reference material
- extracts the most portable final look layer
- runs the external ZIP pack through the current loader’s real post-effect pipeline

---

## Usage

### 1. Place a shader pack
Put the ZIP shader pack into:

`run/shaderpacks/`

### 2. Select the active pack
Edit the config file:

`run/config/vulkanpostfx.json`

Example:

```json
{
  "active_pack_id": "verdantlight130vulkan"
}
````

### 3. Launch the game

When the client starts, the loader will:

* scan ZIP files
* parse the active pack
* materialize runtime resources
* inject them into the client resource system
* attempt to load the corresponding external post chain

### 4. Toggle the debug / external effect

By default, press `F8` to toggle the current debug / external post-effect.

---

## Architecture Overview

```text
[shaderpacks/*.zip]
        |
        v
[Manifest Parse]
        |
        v
[Active Pack Selection]
        |
        v
[Runtime Materialization]
        |
        v
[Runtime Resource Pack Injection]
        |
        v
[post_effect/main.json]
        |
        v
[ShaderManager Post Chain Loading]
        |
        v
[External ZIP Shader Execution]
```

---

## Most Suitable Development Direction Right Now

The recommended next steps are not to keep stuffing more traditional shader files into the project, but to:

1. continue improving `final`-path translation quality
2. correct brightness / exposure mismatches
3. improve fidelity for sky, water, and highlights
4. gradually introduce more complex `composite` semantics
5. eventually explore a more complete Vulkan shader compatibility layer

---

## Design Philosophy

The core idea of this project is not to force all traditional shader semantics into Vulkan at once, but rather:

**first establish a truly executable chain, then gradually bring back the visual logic that matters**

In other words:

* first make external packs actually run
* then gradually make the visuals resemble the original shader pack
* only after that pursue larger-scale compatibility

---

## Notes

This is still a fast-evolving experimental project.
If you are looking for a ready-made, fully compatible replacement for Iris / OptiFine, the current version is not there yet.
But if you care about:

* Vulkan shader workflows
* external ZIP shader pack loading
* an experimental translation framework for modern Minecraft
* gradually migrating traditional shader semantics into a new rendering path

then this project is built exactly for that.

