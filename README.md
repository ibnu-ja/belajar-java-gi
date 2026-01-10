# Hypersonic

Subsonic client made with Java and GTK4+Libadwaita.

### Build & Install (Default: `~/.local`)

```bash
./gradlew shadowJar
meson setup mesonBuild --prefix ~/.local
meson install -C mesonBuild

```

### Custom Install Location

If you change the installation prefix, you must pass the path to Gradle so the application knows where to find resources (locales, etc.).

```bash
# Example: Installing to /usr
./gradlew shadowJar -PmesonPrefix=/usr
meson setup mesonBuild --prefix /usr
sudo meson install -C mesonBuild
```

### Flatpak Build

*To be added later.*
