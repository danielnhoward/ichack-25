'use server';

export async function getTransforms(input: Input): Promise<Transform[]> {
    const response = await fetch(process.env.BACKEND as string, {
        method: 'post',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(input),
    });

    return await response.json();
}
