fn main() {
    let x = 2;

    match x {
        2 | 3 | 4  if y < 5 => println!("x is 2 or 3 or 4"),
        _ => println!("x is not 2 nor 3 nor 4!"),
    }
}
