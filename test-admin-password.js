const fetch = require('node-fetch');

async function resetAdminPassword(newPassword) {
    try {
        const response = await fetch('http://localhost:8080/api/auth/admin/reset-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `newPassword=${encodeURIComponent(newPassword)}`
        });

        if (response.ok) {
            const result = await response.text();
            console.log('✅ Succès:', result);
            return true;
        } else {
            console.log('❌ Erreur:', response.status, response.statusText);
            const errorText = await response.text();
            console.log('Détails:', errorText);
            return false;
        }
    } catch (error) {
        console.log('❌ Erreur de connexion:', error.message);
        return false;
    }
}

// Nouveau mot de passe pour admin@souqly.com
const newPassword = 'admin123';

console.log('🔄 Changement du mot de passe pour admin@souqly.com...');
console.log('Nouveau mot de passe:', newPassword);

resetAdminPassword(newPassword).then(success => {
    if (success) {
        console.log('\n🎉 Mot de passe changé avec succès !');
        console.log('Tu peux maintenant te connecter avec:');
        console.log('Email: admin@souqly.com');
        console.log('Mot de passe:', newPassword);
    } else {
        console.log('\n💥 Échec du changement de mot de passe');
    }
}); 