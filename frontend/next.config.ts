import type {NextConfig} from 'next';

const nextConfig: NextConfig = {
    skipMiddlewareUrlNormalize: true,
    skipTrailingSlashRedirect: true,
};

export default nextConfig;
