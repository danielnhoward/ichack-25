'use server';

export async function getTransforms(input: Input): Promise<Transform[]> {
    const response = await fetch(process.env.BACKEND as string, {
        method: 'post',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(input),
    });

    console.log(input);
    console.log(response);

    const t = await response.json();
    console.log(t);
    return t;
    // return await response.json();
}
