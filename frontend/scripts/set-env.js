const fs = require('fs');
const path = require('path');

// Ensure the directory exists
const envDir = path.resolve(__dirname, '../src/environments');
if (!fs.existsSync(envDir)) {
  fs.mkdirSync(envDir, { recursive: true });
}

const targetPath = path.resolve(envDir, 'environment.ts');

// Read from system environment or use a placeholder
const mapboxToken = process.env.MAPBOX_TOKEN || 'YOUR_MAPBOX_TOKEN_HERE';

const envConfigFile = `export const environment = {
  production: ${process.env.NODE_ENV === 'production' ? 'true' : 'false'},
  mapboxToken: '${mapboxToken}'
};
`;

console.log(`Generating Angular environment.ts at ${targetPath}...`);
fs.writeFileSync(targetPath, envConfigFile);
console.log(`Done! Mapbox Token set to: ${mapboxToken.substring(0, 8)}...`);
