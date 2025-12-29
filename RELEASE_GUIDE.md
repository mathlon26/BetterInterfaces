# How to Release BetterInterfaces-1.0.1.jar on GitHub

## Step-by-Step Instructions

### 1. Ensure your changes are committed

```bash
# Check current status
git status

# Add and commit changes (if needed)
git add .
git commit -m "Prepare for release 1.0.1"
git push origin main
```

### 2. Build the JAR file (if not already built)

```bash
./gradlew clean build
```

The JAR will be located at: `build/libs/BetterInterfaces-1.0.1.jar`

### 3. Create a Git tag for the release

```bash
# Create an annotated tag
git tag -a v1.0.1 -m "Release version 1.0.1"

# Push the tag to GitHub
git push origin v1.0.1
```

**Alternative: Create tag from GitHub web UI** (see step 4)

### 4. Create the GitHub Release

#### Option A: Using GitHub Web UI (Recommended)

1. Go to your repository: https://github.com/mathlon26/BetterInterfaces
2. Click on **"Releases"** (right sidebar) or go to: https://github.com/mathlon26/BetterInterfaces/releases
3. Click **"Draft a new release"** or **"Create a new release"**
4. Fill in the release details:
   - **Tag**: Select `v1.0.1` or create a new tag `v1.0.1`
   - **Title**: `BetterInterfaces 1.0.1` or `v1.0.1`
   - **Description**: Add release notes, for example:
     ```
     ## BetterInterfaces 1.0.1
     
     ### Features
     - Full menu system with event handling
     - Pageable menus with navigation
     - Menu context and navigation stack
     - Back and close buttons
     - Fill utilities and gradients
     - Comprehensive examples and documentation
     
     ### Documentation
     See README.md for full developer documentation.
     
     ### Installation
     Download `BetterInterfaces-1.0.1.jar` and place it in your server's `plugins` folder.
     ```
   - **Attach binaries**: Drag and drop or click to upload `build/libs/BetterInterfaces-1.0.1.jar`
5. Click **"Publish release"**

#### Option B: Using GitHub CLI (if installed)

```bash
# Install GitHub CLI first if needed (optional)
# Then create release:
gh release create v1.0.1 \
  build/libs/BetterInterfaces-1.0.1.jar \
  --title "BetterInterfaces 1.0.1" \
  --notes "Release version 1.0.1 with full menu system features"
```

### 5. Verify the Release

1. Visit: https://github.com/mathlon26/BetterInterfaces/releases
2. You should see your release with the JAR file attached
3. Users can now download the JAR from the release page

## Quick Command Reference

```bash
# Full release workflow (if all steps needed):
git add .
git commit -m "Prepare for release 1.0.1"
git push origin main

./gradlew clean build

git tag -a v1.0.1 -m "Release version 1.0.1"
git push origin v1.0.1

# Then create release via GitHub web UI and upload build/libs/BetterInterfaces-1.0.1.jar
```

## Notes

- The JAR file is located at: `build/libs/BetterInterfaces-1.0.1.jar`
- Tag format: `v1.0.1` (with 'v' prefix is recommended)
- Always test the JAR before releasing
- Consider creating a `CHANGELOG.md` to track changes between versions

