const fs = require('fs');
const path = require('path');

// Ensure the directory exists
const envDir = path.resolve(__dirname, '../src/environments');
if (!fs.existsSync(envDir)) {
  fs.mkdirSync(envDir, { recursive: true });
}

const targetPath = path.resolve(envDir, 'environment.ts');

const envConfigFile = `export const environment = {
  production: ${process.env.NODE_ENV === 'production' ? 'true' : 'false'}
};
`;

console.log(`Generating Angular environment.ts at ${targetPath}...`);
fs.writeFileSync(targetPath, envConfigFile);
console.log(`Done!`);
