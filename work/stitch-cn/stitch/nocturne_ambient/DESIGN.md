---
name: Nocturne Ambient
colors:
  surface: '#111317'
  surface-dim: '#111317'
  surface-bright: '#37393e'
  surface-container-lowest: '#0c0e12'
  surface-container-low: '#1a1c20'
  surface-container: '#1e2024'
  surface-container-high: '#282a2e'
  surface-container-highest: '#333539'
  on-surface: '#e2e2e8'
  on-surface-variant: '#bec9c7'
  inverse-surface: '#e2e2e8'
  inverse-on-surface: '#2f3035'
  outline: '#889391'
  outline-variant: '#3f4948'
  surface-tint: '#8ad3ce'
  primary: '#8ad3ce'
  on-primary: '#003735'
  primary-container: '#5fa8a3'
  on-primary-container: '#003a38'
  inverse-primary: '#166965'
  secondary: '#accae3'
  on-secondary: '#133347'
  secondary-container: '#2e4c61'
  on-secondary-container: '#9ebcd4'
  tertiary: '#ffb599'
  on-tertiary: '#53220d'
  tertiary-container: '#d38a6e'
  on-tertiary-container: '#572510'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#a6f0ea'
  primary-fixed-dim: '#8ad3ce'
  on-primary-fixed: '#00201e'
  on-primary-fixed-variant: '#00504c'
  secondary-fixed: '#c8e6ff'
  secondary-fixed-dim: '#accae3'
  on-secondary-fixed: '#001e2e'
  on-secondary-fixed-variant: '#2c4a5e'
  tertiary-fixed: '#ffdbce'
  tertiary-fixed-dim: '#ffb599'
  on-tertiary-fixed: '#370e00'
  on-tertiary-fixed-variant: '#6e3721'
  background: '#111317'
  on-background: '#e2e2e8'
  surface-variant: '#333539'
typography:
  display:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '500'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.05em
  label-sm:
    fontFamily: Inter
    fontSize: 10px
    fontWeight: '500'
    lineHeight: 14px
    letterSpacing: 0.05em
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  base: 8px
  xs: 4px
  sm: 12px
  md: 16px
  lg: 24px
  xl: 32px
  container_margin: 20px
  touch_target_min: 48px
---

## Brand & Style

The design system is centered on the concept of "Digital Somnolence"—a UI that recedes into the background to facilitate rest. It targets users seeking a sanctuary from high-stimulation interfaces, providing a calm, low-distraction environment for sleep and meditation.

The visual style is **Minimalist-Modern** with a focus on **Tonal Layering**. It avoids harsh pure blacks in favor of deep, atmospheric charcoals to reduce eye strain in pitch-black environments. The aesthetic is professional, quiet, and premium, evoking the feeling of a high-end physical sleep device. 

**Emotional Response:**
- **Quietude:** Every element feels weighted and still.
- **Trust:** Precision in typography and spacing suggests reliability.
- **Ease:** Large tap targets and clear iconography ensure the app is usable even in a state of drowsiness.

## Colors

The palette is designed for deep-night usage. The base is a custom charcoal (#0F1115), preventing the "starkness" of pure black while maintaining OLED efficiency. 

- **Primary (Muted Teal):** Used for active states and primary actions. It is desaturated to prevent "blooming" on high-brightness screens.
- **Secondary (Soft Blue):** Reserved for atmospheric elements and mixer status.
- **Accent (Warm Amber):** Used sparingly for "soft" notifications, timer indicators, or sunset-related sleep sounds.
- **Danger:** A desaturated "Soft Red" that provides enough contrast to be functional without being jarring.

## Typography

This design system utilizes **Inter** for its exceptional legibility and systematic feel. 

- **Greetings & Titles:** Use `display` and `headline-lg` for "Tonight" or "Good Evening" messages to provide a warm, clear entry point.
- **Sound Titles:** Use `headline-md` for sound names on cards to ensure they are readable at a glance.
- **Utility & Metadata:** Use `label-lg` with uppercase styling for durations and categories to create a clear visual distinction from body text.
- **Weighting:** Light and Regular weights are preferred for body text to maintain a "light" visual footprint against the dark background.

## Layout & Spacing

Designed for the 390x844 (Android/iOS modern) viewport, the layout follows an 8px rhythmic grid. 

- **Grid:** A 4-column fluid grid is used for mobile. 
- **Tap Targets:** In line with night-time ergonomics, all interactive elements (Play, Pause, Mix) adhere to a minimum 48px height/width.
- **Margins:** A consistent 20px side margin provides breathing room, preventing the UI from feeling cramped.
- **Vertical Rhythm:** Large `xl` (32px) gaps are used between major sections (e.g., between the "Tonight" greeting and the "Quick Start" sound grid) to reduce cognitive load.

## Elevation & Depth

This design system avoids traditional drop shadows to maintain a "flat, calm" surface. Depth is communicated through **Tonal Layering**:

- **Level 0 (Background):** #0F1115 - The canvas.
- **Level 1 (Surface):** #1A1D23 - For sound cards, sliders, and navigation bars.
- **Level 2 (Surface Variant):** #262A31 - For active states or "hover" effects (if applicable) and inset inputs.

**Outlines:** Use subtle, low-opacity borders (1px, 10% white) on Level 1 surfaces to define edges without adding visual noise.

## Shapes

The design system uses a **Soft (Level 1)** roundedness logic. 

- **Base Radius:** 8px (0.5rem) is applied to all cards, buttons, and input fields.
- **Large Elements:** Persistent players or bottom sheets use `rounded-lg` (12px/0.75rem) on the top corners to feel more organic and approachable.
- **Interaction Feedback:** Selection indicators (like a ring around a sound card) should follow the exact radius of the parent container.

## Components

- **Sound Cards:** High-surface-area cards with a `surface` background. Icons should be centered, utilizing `primary_color` for active states.
- **Mixer Sliders:** Custom horizontal tracks with a thickness of 6px. The "thumb" should be a large 24px circle to ensure ease of use in the dark.
- **Mini-Player:** A persistent bar anchored to the bottom. It uses a slight `surface_variant` color to separate it from the main content.
- **Buttons:**
    - *Primary:* Filled with `primary_color`, text in `neutral_color_hex` for maximum contrast.
    - *Ghost:* Outlined in `text_low_emphasis` for secondary actions like "Add to Library."
- **Chips:** Used for categories (e.g., "Rain," "White Noise"). Small 8px radius with `surface_variant` backgrounds.
- **Checkboxes/Radio Buttons:** Circular and oversized (24px) to align with the soft, rounded aesthetic.