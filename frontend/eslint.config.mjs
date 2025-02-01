import {fixupConfigRules, fixupPluginRules} from '@eslint/compat';
import {FlatCompat} from '@eslint/eslintrc';
import js from '@eslint/js';
import typescriptEslint from '@typescript-eslint/eslint-plugin';
import tsParser from '@typescript-eslint/parser';
import _import from 'eslint-plugin-import';
import path from 'node:path';
import {fileURLToPath} from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const compat = new FlatCompat({
    baseDirectory: __dirname,
    recommendedConfig: js.configs.recommended,
    allConfig: js.configs.all,
});

const config = [...fixupConfigRules(compat.extends(
    'next/core-web-vitals',
    'next/typescript',
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
    'google',
    'plugin:import/typescript',
)), {
    plugins: {
        'import': fixupPluginRules(_import),
        '@typescript-eslint': fixupPluginRules(typescriptEslint),
    },

    languageOptions: {
        parser: tsParser,
    },

    settings: {
        'import/resolver': {
            node: {
                paths: ['src', 'emails', 'dev'],
                extensions: ['.js', '.jsx', '.ts', '.tsx'],
            },
        },
    },

    rules: {
        'require-jsdoc': 'off',
        'indent': ['warn', 4],

        'no-tabs': ['warn', {
            allowIndentationTabs: true,
        }],

        'no-unused-vars': 'off',
        '@typescript-eslint/consistent-type-imports': 'warn',

        'import/order': ['warn', {
            'groups': [
                ['internal', 'parent', 'sibling', 'index'],
                ['builtin', 'external'],
                'type',
            ],

            'newlines-between': 'always',

            'alphabetize': {
                order: 'asc',
                caseInsensitive: true,
            },
        }],

        'max-len': 'off',
        'linebreak-style': ['warn', 'unix'],
        'new-cap': 'off',
        'jsx-quotes': ['warn', 'prefer-double'],

        '@typescript-eslint/no-restricted-imports': ['warn', {
            patterns: ['../'],
        }],

        'valid-jsdoc': 'off',
    },
}];

export default config;
