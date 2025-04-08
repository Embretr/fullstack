<script setup lang="ts">
import UserSettings from '@/components/UserSettings.vue';
</script>
<template>
    <div class="user-settings-view">
        <h1>{{ $t('userProfile.settingsButton') }}</h1>
        <UserSettings />
    </div>
</template>
<style scoped>
    .user-settings-view {
        padding: 2rem;
    }
    h1 {
        text-align: center;
        margin-bottom: 2rem;
        color: var(--primary-color);
    }
</style>